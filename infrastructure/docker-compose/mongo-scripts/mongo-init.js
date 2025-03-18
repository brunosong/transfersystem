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

db.learning_materials.insertMany([
    {
        "title": "Introduction to Java",
        "description": "Basic Java programming concepts",
        "learningLevel": 1,
        "metadataList": [
            { "attributeName": "John Doe", "attributeValue": "120" },
            { "attributeName": "PDF", "attributeValue": "English" }
        ]
    },
    {
        "title": "Advanced Java",
        "description": "In-depth Java programming techniques",
        "learningLevel": 3,
        "metadataList": [
            { "attributeName": "Jane Smith", "attributeValue": "300" },
            { "attributeName": "eBook", "attributeValue": "English" }
        ]
    },
    {
        "title": "Spring Boot Basics",
        "description": "Getting started with Spring Boot",
        "learningLevel": 2,
        "metadataList": [
            { "attributeName": "Alex Brown", "attributeValue": "200" },
            { "attributeName": "Video", "attributeValue": "English" }
        ]
    }
]);



