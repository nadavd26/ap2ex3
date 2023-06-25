const express = require('express');
const router = express.Router();

const tokenController = require('../controllers/tokenController');

// Route to create a new token
router.post('/', tokenController.createToken);

module.exports = router;
