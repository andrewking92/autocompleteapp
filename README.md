{
  "mappings": {
    "dynamic": false,
    "fields": {
      "firstName": {
        "type": "autocomplete"
      },
      "lastName": {
        "type": "autocomplete"
      },
      "nickName": {
        "type": "autocomplete"
      }
    }
  }
}





QUERY_STRING="litt"

const agg = [
{
    $search: {
        index: "names",
        compound: {
            should: [
                {
                    autocomplete: {
                        query: QUERY_STRING,
                        path: "firstName"
                    }
                },
                {
                    autocomplete: {
                        query: QUERY_STRING,
                        path: "lastName"
                    }
                },
                {
                    autocomplete: {
                        query: QUERY_STRING,
                        path: "nickName"
                    }
                }
            ],
            minimumShouldMatch: 1
        }
    }
},
{ $limit: 5 }
];

db.names.aggregate(agg)




const agg = [
{
    $search: {
        index: "names",
        compound: {
            should: [
                {
                    autocomplete: {
                        query: QUERY_STRING,
                        path: "firstName",
                        score: { boost: { value: 2 } }
                    }
                },
                {
                    autocomplete: {
                        query: QUERY_STRING,
                        path: "lastName",
                        score: { boost: { value: 2 } }
                    }
                },
                {
                    autocomplete: {
                        query: QUERY_STRING,
                        path: "nickName",
                        score: { boost: { value: 2 } }
                    }
                }
            ],
            minimumShouldMatch: 1
        },
        highlight: {
            path: ["firstName", "lastName", "nickName"]
        }
    }
},
{
    $project: {
        firstName: 1,
        lastName: 1,
        nickName: 1,
        score: { $meta: "searchScore" },
        highlight: { $meta: "searchHighlights" }
    }
},
{ $limit: 5 }
];

db.names.aggregate(agg)
