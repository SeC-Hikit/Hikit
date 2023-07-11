
db.getCollection("core.Trail").updateMany({}, {$set : { "municipalities": []}} )

db.getCollection("core.Poi").updateMany({}, {$set : { "externalId": ""}} )
db.getCollection("core.Poi").updateMany({}, {$set : { "externalSystemName": ""}} )