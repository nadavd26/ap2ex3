const User = require('../models/userModel');
const jwt = require("jsonwebtoken")
const key = "Some super secret key shhhhhhhhhhhhhhhhh!!!!!"

// Controller function to handle token creation
async function createToken(req, res) {
    try {
        if (!(req.body.hasOwnProperty('username') &&
        req.body.hasOwnProperty('password'))) {
        return res.status(400).json({ title: 'One or more validation errors occurred.' });
      }
        const { username, password } = req.body;
        if (!(typeof username === 'string' &&
        typeof password === 'string')) {
        return res.status(400).json({ title: 'One or more validation errors occurred.' });
      }
        const user = await User.findOne({ username: username, password: password });
        if (!user) {
            return res.status(404).send('username or password invalid');
        }
        const data = { username: username, password: password }
        // Generate the token.
        const token = jwt.sign(data, key)
        if(req.body.hasOwnProperty("fireBaseToken")){
            const resp = await User.updateOne({ username: username }, { $set: { fireBaseToken: req.body.fireBaseToken } });
            // console.log(resp)
        }
        return res.status(200).json(token);
    } catch (error) {
        return res.status(500).send('Failed to create token' );
    }
}

function verifyToken(req) {
    try {
        const token = req.headers.authorization.split(" ")[1];
        const cleanedToken = token.replace(/"/g, "");
        // Verify the token is valid
        const userOfTheToken = jwt.verify(cleanedToken, key)
        return userOfTheToken
    } catch (err) {
        console.log(err)
        return null;
    }
}

// Export the controller functions
module.exports = {
    createToken,
    verifyToken
};
