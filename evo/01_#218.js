db.getCollection("core.Place").updateMany({}, {$set : { "isDynamic": false}} )
db.getCollection("core.Trail").updateMany({}, {$set : { "locations.$[].isDynamic": false}} )