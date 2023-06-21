const mongoose = require('mongoose');

const chatSchema = new mongoose.Schema({
  id: {
    type: String,
    required: true
  },
  users: [
    {
      username: {
        type: String,
        required: true
      },
      displayName: {
        type: String,
        required: true
      },
      profilePic: {
        type: String,
        required: true
      }
    }
  ],
  messages: [
    {
      id: {
        type: String,
        required: true
      },
      created: {
        type: Date,
        required: true,
        default: Date.now
      },
      sender: {
        username: {
          type: String,
          required: true
        },
        displayName: {
          type: String,
          required: true
        },
        profilePic: {
          type: String,
          required: true
        }
      },
      content: {
        type: String,
        required: true
      }
    }
  ]
});

const Chat = mongoose.model('Chats', chatSchema);

module.exports = Chat;
