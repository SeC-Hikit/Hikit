let collections = ["core.Media", "core.Trail"];

collections.forEach((collection)=> {
    db.getCollection(collection).updateMany({}, {$rename:{"fileDetails": "recordDetails"}})
});