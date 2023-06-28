const { verifyToken } = require('./tokenController');
const Chat = require('../models/chatModel');
const User = require('../models/userModel');
const io = global.io
const admin = require('firebase-admin');

var serviceAccount = require("../serviceAccountKey.json");

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount)
});
// //for io
// const express = require('express');
// const app = express();
// const http = require('http');
// const server = http.createServer(app);


var users_sockets = []

function usersSocketsAdd(username, socket) {
    const existingUserIndex = users_sockets.findIndex((user) => user.username === username);

    if (existingUserIndex !== -1) {
        users_sockets[existingUserIndex] = { username, socket };
    } else {
        users_sockets.push({ username, socket });
    }
}

function usersSocketsRemove(socket) {
    const socketIndex = users_sockets.findIndex((user) => user.socket === socket);

    if (socketIndex !== -1) {
        // Remove the user from the array
        users_sockets.splice(socketIndex, 1);
    }
}

async function sendToFireBase(reciever, sender, message, chatID, created, profilePic) {
    const recieverUser = await User.findOne({ username: reciever });
    const fireBaseToken = recieverUser.fireBaseToken;
    if (fireBaseToken === "") {
        return;
    }
    let title = "message from: " + sender;
    const fireBaseMessage = {
        notification: {
            title: title,
            body: message
        },
        token: fireBaseToken,
        data: {
            chatID: chatID,
            created: created,
            username: reciever,
            profilePic: profilePic
        }
    };
    admin
        .messaging()
        .send(fireBaseMessage)
        .then((response) => {})
        .catch((error) => {
            console.log(error)
        });
}

async function getNextChatId() {
    try {
        const allChats = await Chat.find();
        let max = -1;
        allChats.forEach(chat => {
            if (parseInt(chat.id, 10) > max)
                max = parseInt(chat.id, 10);
        });
        return (max + 1).toString();
    } catch (error) {
        console.error("Error retrieving next chat ID:", error);
        return -1;
    }
}



function getNextMessageId(messages) {
    if (messages.length === 0) {
        return 0;
    }
    return parseInt(messages[messages.length - 1].id, 10) + 1;
}

async function createMessage(req, res) {
    if (!(req.body.hasOwnProperty('msg') &&
        req.params.hasOwnProperty('id'))) {
        return res.status(400).json({ title: 'One or more validation errors occurred.' });
    }
    const { id } = req.params;
    const { msg } = req.body;
    if (!(typeof msg === 'string' && typeof id === 'string')) {
        return res.status(400).json({ title: 'One or more validation errors occurred.' });
    }
    const senderUsername = verifyToken(req)
    if (senderUsername === null) {
        return res.status(401).send('unauthorized')
    }
    const chat = await Chat.findOne({ id: id })
    if (!chat) {
        return res.status(500).send('server error');
    }
    var messages = chat.messages
    const messageId = getNextMessageId(messages)
    const sender = await User.findOne({ username: senderUsername.username });
    if (!sender) {
        return res.status(404).send('server error');
    }
    if (!(sender.username === chat.users[0].username || sender.username === chat.users[1].username)) {
        return res.status(401).send('unauthorized');
    }
    const jsonSender = { username: sender.username, displayName: sender.displayName, profilePic: sender.profilePic }
    const newMessageJSON = { id: messageId, sender: jsonSender, content: msg }
    messages.push(newMessageJSON)
    await Chat.updateOne({ id: id }, { $set: { messages: messages } })
    const updatedChat = await Chat.findOne({ id: id })

    //sending to socket
    var reciever_username = chat.users[0].username
    if (reciever_username === sender.username) {
        reciever_username = chat.users[1].username
    }
    const userEntry = users_sockets.find((user) => user.username === reciever_username);
    const recieverSocket = userEntry ? userEntry.socket : null;
    if (!(recieverSocket === null)) {
        //the reciever is not connected
        io.to(recieverSocket.id).emit('message', { "sender": sender.username, "id": id })
    }
    
    const resu = await sendToFireBase(reciever_username, jsonSender.displayName, msg, id, updatedChat.messages[messageId].created.toISOString(), sender.profilePic)
    return res.status(200).json(updatedChat.messages[messageId]);
}

async function createChat(req, res) {
    try {
        if (!(req.body.hasOwnProperty('username'))) {
            return res.status(400).json({ title: 'One or more validation errors occurred.' });
        }
        const { username } = req.body;
        if (!(typeof username === 'string')) {
            return res.status(400).json({ title: 'One or more validation errors occurred.' });
        }
        const userOfTheToken = verifyToken(req)
        if (userOfTheToken === null) {
            return res.status(401).send('unauthorized')
        }
        if (userOfTheToken.username === username) {
            return res.status(400).send('Thou shalt not talk with thy self')
        }
        const user1 = await User.findOne({ username: userOfTheToken.username });
        if (!user1) {
            return res.status(404).send('No such user');
        }
        const jsonUser1 = { username: user1.username, displayName: user1.displayName, profilePic: user1.profilePic }
        const user2 = await User.findOne({ username: username });
        if (!user2) {
            return res.status(404).send('No such user');
        }
        const jsonUser2 = { username: user2.username, displayName: user2.displayName, profilePic: user2.profilePic }
        const id = await getNextChatId()
        if (id === -1) {
            return res.status(500).send('Failed to get last id');
        }
        var users = []
        users.push(jsonUser1)
        users.push(jsonUser2)
        var messages = []
        await Chat.create({ id, users, messages });
        const response = { id: id, user: jsonUser2 }
        return res.status(200).json(response);
    } catch (error) {
        return res.status(500).send('Failed to create chat');
    }
}

async function getAllMessages(req, res) {
    if (!(req.params.hasOwnProperty('id'))) {
        return res.status(400).json({ title: 'One or more validation errors occurred.' });
    }
    const { id } = req.params;
    if (!(typeof id === 'string')) {
        return res.status(400).json({ title: 'One or more validation errors occurred.' });
    }
    const sender = verifyToken(req)
    if (sender === null) {
        return res.status(401).send('unauthorized')
    }
    const chat = await Chat.findOne({ id: id })
    if (!chat) {
        return res.status(404).send('Chat not found');
    }
    if (!(sender.username === chat.users[0].username || sender.username === chat.users[1].username)) {
        return res.status(401).send('unauthorized');
    }
    const arr = chat.messages.reverse()
    return res.status(200).json(arr);
}

async function getAllChats(req, res) {
    const sender = verifyToken(req)
    if (sender === null) {
        return res.status(401).send('unauthorized')
    }
    const allChats = await Chat.find()
    let userChats = []
    allChats.forEach(chat => {
        if (sender.username === chat.users[0].username) {
            var jsonLastMessage = null
            if (!(chat.messages.length === 0)) {
                const lastMessage = chat.messages[chat.messages.length - 1]
                jsonLastMessage = { id: lastMessage.id, created: lastMessage.created, content: lastMessage.content }
            }
            userChats.push({ id: chat.id, user: chat.users[1], lastMessage: jsonLastMessage })
        }
        if (sender.username === chat.users[1].username) {
            var jsonLastMessage = null
            if (!(chat.messages.length === 0)) {
                const lastMessage = chat.messages[chat.messages.length - 1]
                jsonLastMessage = { id: lastMessage.id, created: lastMessage.created, content: lastMessage.content }
            }
            userChats.push({ id: chat.id, user: chat.users[0], lastMessage: jsonLastMessage })
        }
    });
    return res.status(200).json(userChats)
}

async function getChatById(req, res) {
    if (!(req.params.hasOwnProperty('id'))) {
        return res.status(400).json({ title: 'One or more validation errors occurred.' });
    }
    const { id } = req.params;
    if (!(typeof id === 'string')) {
        return res.status(400).json({ title: 'One or more validation errors occurred.' });
    }
    const sender = verifyToken(req)
    if (sender === null) {
        return res.status(401).send('unauthorized')
    }
    const chat = await Chat.findOne({ id: id })
    if (!chat) {
        return res.status(404).json({ error: 'Chat not found' });
    }
    if (!(sender.username === chat.users[0].username || sender.username === chat.users[1].username)) {
        return res.status(404).json({ error: 'client not in the chat' });
    }
    return res.status(200).json(chat)
}

async function deleteChatById(req, res) {
    if (!(req.params.hasOwnProperty('id'))) {
        return res.status(400).json({ title: 'One or more validation errors occurred.' });
    }
    const { id } = req.params;
    if (!(typeof id === 'string')) {
        return res.status(400).json({ title: 'One or more validation errors occurred.' });
    }
    const sender = verifyToken(req)
    if (sender === null) {
        return res.status(401).send('unauthorized')
    }
    const chat = await Chat.findOne({ id: id })
    if (!chat) {
        return res.status(404).send('Chat not found');
    }
    if (!(sender.username === chat.users[0].username || sender.username === chat.users[1].username)) {
        return res.status(404).send('client not in the chat');
    }
    const result = await Chat.deleteOne({ id: id })
    return res.status(200).json(result)
}

module.exports = {
    createChat,
    createMessage,
    getAllMessages,
    getAllChats,
    getChatById,
    deleteChatById,
    usersSocketsAdd,
    usersSocketsRemove
};
