const express = require('express');
// Create express app
const app = express();
const mongoose = require('mongoose');
const path = require('path');
const cors = require('cors');
const http = require('http');
// Create HTTP server using the Express app
const server = http.createServer(app);
const { Server } = require('socket.io')
//const io = new Server(server)
const io = new Server(server, {
    cors: {
        origin: "http://localhost:3000",
        methods: ["GET", "POST"]
    }
});

global.io = io;

const { usersSocketsAdd, usersSocketsRemove } = require('./controllers/chatController')
// Middleware
app.use(express.json()); // Parse JSON request bodies
// Connect to MongoDB
app.use(cors());
mongoose.connect('mongodb://127.0.0.1:27017/api', {
    useNewUrlParser: true,
    useUnifiedTopology: true,
})
    .then(() => {
        console.log('Connected to MongoDB');
    })
    .catch((error) => {
        console.error('Failed to connect to MongoDB', error);
    });

// Require routes
const userRoutes = require('./routes/userRoutes');
const tokenRoutes = require('./routes/tokenRoutes');
const chatRoutes = require('./routes/chatRoutes');
// Routes
app.use('/api/Users', userRoutes);
app.use('/api/Tokens', tokenRoutes);
app.use('/api/Chats', chatRoutes);


app.use(express.static(path.join(__dirname, 'public')));

// Route handler for the root route
app.get('/', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'index.html'));
});


//socket io
    //socket part
    io.on('connection', (socket) => {
        socket.on('username', (username) => {
            usersSocketsAdd(username, socket)
        })
        // Handle disconnections
        socket.on('disconnect', () => {
            usersSocketsRemove(socket)
        });
    });

// Start the server
server.listen(5000, () => {
    console.log('Server is running on port 5000');
});