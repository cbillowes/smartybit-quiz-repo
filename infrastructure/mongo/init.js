// Connect to the admin database
db = connect('mongodb://localhost:27017/admin');

// Create a user with access to the `smartybit-profiles` database
db.createUser({
  user: 'myuser',
  pwd: 'mypassword',
  roles: [{ role: 'readWrite', db: 'smartybit-profiles' }],
});
