let collections = ["core.Media", "core.Trail", "core.Raw"];

collections.forEach((collection)=> {
    db.getCollection(collection).updateMany({}, {$rename:{"fileDetails": "recordDetails"}})
});