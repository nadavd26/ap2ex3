const express = require('express');
const router = express.Router();

const chatController = require('../controllers/chatController');

// Route to create a new token
router.post('/', chatController.createChat);
router.get('/', chatController.getAllChats);
router.get('/:id', chatController.getChatById);
router.delete('/:id', chatController.deleteChatById)
router.post('/:id/Messages', chatController.createMessage);
router.get('/:id/Messages', chatController.getAllMessages);

module.exports = router;
