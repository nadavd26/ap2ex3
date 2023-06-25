const { verifyToken } = require('./tokenController');

const User = require('../models/userModel');

// Controller function to handle user creation
async function createUser(req, res) {
  try {
    if (!(req.body.hasOwnProperty('username') &&
      req.body.hasOwnProperty('password') &&
      req.body.hasOwnProperty('displayName') &&
      req.body.hasOwnProperty('profilePic'))) {
      return res.status(400).json({ title: 'One or more validation errors occurred.' });
    }
    const { username, password, displayName, profilePic } = req.body;
    if (!(typeof username === 'string' &&
      typeof password === 'string' &&
      typeof displayName === 'string' &&
      typeof profilePic === 'string')) {
      return res.status(400).json({ title: 'One or more validation errors occurred.' });
    }
    const user = await User.findOne({ username: username });
    if (user) {
      return res.status(409).json({ error: 'user already exists' });
    }
    const newUser = await User.create({ username, password, displayName, profilePic });
    return res.status(200).json(newUser);
  } catch (error) {
    return res.status(500).json({ error: 'Failed to create user' });
  }
}

// Controller function to get a single user by username
async function getUserByUsername(req, res) {
  try {
    if (!(req.params.hasOwnProperty('username'))) {
      return res.status(400).json({ title: 'One or more validation errors occurred.' });
    }
    const { username } = req.params;
    if (!(typeof username === 'string')) {
    return res.status(400).json({ title: 'One or more validation errors occurred.' });
  }
    if (req.headers.authorization) {
      // Extract the token from that header
      const userOfTheToken = verifyToken(req)
      if (userOfTheToken === null) {
        return res.status(401).send('unauthorized')
      }
      if (!(userOfTheToken.username === username))
        return res.status(401).send('unauthorized')
    }
    else
      return res.status(401).send('unauthorized');
    const user = await User.findOne({ username: username });
    if (!user) {
      return res.status(404).send('User not found');
    }
    const jsonUser = { username: user.username, displayName: user.displayName, profilePic: user.profilePic }
    return res.status(200).json(jsonUser);
  } catch (error) {
    return res.status(500).send('Failed to retrieve user');
  }
}

// Export the controller functions
module.exports = {
  createUser,
  getUserByUsername
};
