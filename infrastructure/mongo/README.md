# Mongo DB Troubleshooting Guide

## MongoDB Initialization Script Not Running in Docker Container

When using a MongoDB initialization script (`init.js`) in a Docker container, you may encounter issues where the script does not run as expected. This guide provides steps to ensure the initialization script executes correctly.

Docker does cache files, including initialization scripts, if the container or its data volume already exists. To ensure Docker does not reuse a cached version of your `init.js` file, follow these steps:

---

### **1. Remove Existing MongoDB Containers and Volumes**

The `init.js` file is executed only on the first initialization of a new MongoDB container. If the container or its data volume already exists, Docker skips running the initialization script.

Run the following commands to remove the existing MongoDB container and its volumes:

```bash
docker-compose down -v
```

This command stops the container and removes all associated volumes, forcing Docker to reinitialize the database.

---

### **2. Force No Caching for `init.js`**

To ensure Docker does not cache your `init.js` file, bind-mount the script in your `docker-compose.yml` file instead of copying it into the container.

#### Updated `docker-compose.yml`:

```yaml
version: '3.8'
services:
  mongodb:
    image: mongo:latest
    container_name: mongodb
    ports:
      - '27017:27017'
    environment:
      MONGO_INITDB_ROOT_USERNAME: admin
      MONGO_INITDB_ROOT_PASSWORD: adminpassword
    volumes:
      - ./init.js:/docker-entrypoint-initdb.d/init.js:ro # Bind-mount init.js
    restart: unless-stopped
```

- The `:ro` ensures the file is mounted as read-only.

---

### **3. Use a Fresh Volume**

Docker volumes store persistent data for containers. If a volume already exists, MongoDB won’t reinitialize the database, even if you replace `init.js`.

- Use a new volume for testing by running:
  ```bash
  docker-compose up -V
  ```
  The `-V` flag removes existing volumes and recreates them.

---

### **4. Ensure Proper Script Changes**

If Docker doesn't detect changes to `init.js`, ensure the file is saved and its modification time is updated. You can manually touch the file to update its timestamp:

```bash
touch init.js
```

This ensures that Docker notices a change when restarting the container.

---

### **5. Restart Fresh**

Run the following commands to start fresh:

1. Stop and remove existing containers and volumes:
   ```bash
   docker-compose down -v
   ```
2. Build and run the container again:
   ```bash
   docker-compose up --build
   ```

---

### **6. Check Logs to Verify Initialization**

After starting the container, check the logs to ensure the `init.js` file is executed:

```bash
docker logs mongodb
```

Look for messages indicating that:

1. The user was created.
2. The database was accessed or modified.

---

### **7. Optional: Use Named Volumes**

For consistent management of volumes, you can explicitly define a named volume in `docker-compose.yml`:

```yaml
version: '3.8'
services:
  mongodb:
    image: mongo:latest
    container_name: mongodb
    ports:
      - '27017:27017'
    environment:
      MONGO_INITDB_ROOT_USERNAME: admin
      MONGO_INITDB_ROOT_PASSWORD: adminpassword
    volumes:
      - mongo_data:/data/db
      - ./init.js:/docker-entrypoint-initdb.d/init.js:ro
    restart: unless-stopped

volumes:
  mongo_data:
```

When running fresh:

```bash
docker-compose down -v && docker-compose up
```

---

### **Summary**

1. Stop and remove existing containers and volumes: `docker-compose down -v`.
2. Use `docker-compose up -V` to ensure volumes are recreated.
3. Bind-mount `init.js` in `docker-compose.yml`.
4. Restart with `docker-compose up --build`.

Let me know if the problem persists or if you encounter further issues! 🚀
