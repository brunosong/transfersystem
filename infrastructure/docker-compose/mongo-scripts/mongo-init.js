db = db.getSiblingDB('exampledb');
db.createRole({
    role: "learningMaterialsRole",
    privileges: [
        { resource: { db: "exampledb", collection: "learning_materials" }, actions: ["find", "insert", "update", "remove"] }
    ],
    roles: []
});
db.createUser({
    user: "learning_user",
    pwd: "learning_pass123",
    roles: [{ role: "learningMaterialsRole", db: "exampledb" }]
});