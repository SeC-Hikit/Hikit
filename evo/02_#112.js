let collections = db.getCollectionNames();
let excludedCollections = ["core.Instance", "core.TrailDatasetVersion"];

collections.forEach((collectionName)=>{
    if(!excludedCollections.includes(collectionName)) {

        // For each collection loop all documents to add the new attribute
        let ids = db.getCollection(collectionName).find().project({_id: 1}).toArray();

        ids.forEach((documentId)=> {

            let foundId = documentId._id;
            let foundElement = db.getCollection(collectionName).find({"_id": foundId}).toArray()[0];
            if(foundElement.recordDetails) {

                console.log("update RECORD details: " + foundId)
                db.getCollection(collectionName).update({_id:  foundId}, {$set: {
                        "recordDetails.lastModifiedBy" : foundElement.recordDetails.uploadedBy
                    }});
            }
        });
    }
})