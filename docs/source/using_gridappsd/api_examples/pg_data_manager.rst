The Powergrid Model Data Manager API allows you to query the powergrid model data store.

QUERY    - Returns results from a generic SPARQL query against one or all models
-	modelId  (optional)  - when specified it searches against that model, if empty it will search against all models
-	queryString  - SPARQL query, for more information see https://www.w3.org/TR/rdf-sparql-query/   See below for example.
-	resultFormat – XML/JSON ,   The format you wish the result to be returned in.  Can be either json or XML.  Will return result bindings based on the select part of the query string.  See below for example.
For example if you send this request
{
	"requestType": "QUERY",
	"resultFormat": "JSON",
	"queryString": "select ?line_name ?subregion_name ?region_name WHERE {?line rdf:type cim:Line.?line cim:IdentifiedObject.name ?line_name.?line cim:Line.Region ?subregion.?subregion cim:IdentifiedObject.name ?subregion_name.?subregion cim:SubGeographicalRegion.Region ?region.?region cim:IdentifiedObject.name ?region_name}"
}
you will receive the following response
{
  "head": {
    "vars": [ "line_name" , "subregion_name" , "region_name" ]
  } ,
  "results": {
    "bindings": [
      {
        "line_name": { "type": "literal" , "value": "ieee8500" } ,
        "subregion_name": { "type": "literal" , "value": "ieee8500_SubRegion" },
        "region_name": { "type": "literal" , "value": "ieee8500_Region" }
      }
    ]
  }
}


QUERY_OBJECT – Returns details for a particular object based on the object Id
-	modelId (optional) - when specified it searches against that model, if empty it will search against all models
-	objectID – ID of the object you wish to return details for.
-	resultFormat – XML/JSON ,  Will return result bindings based on the select part of the query string.  
QUERY_OBJECT_TYPES – Returns the available object types in the model
-	modelId (optional) - when specified it searches against that model, if empty it will search against all models
-	resultFormat – XML/JSON /CSV,  Will return results as a list in the format selected.
QUERY_MODEL – Returns all or part of the specified model.  Can be filtered by object type
-	modelId - when specified it searches against that model, if empty it will search against all models
-	objectType
-	filter – SPARQL formatted filter string
-	resultFormat – XML/JSON,  Will return result in the format selected.

QUERY_MODEL_NAMES – Returns list of names for all available models
-	Result Format – XML/JSON/CSV
PUT_MODEL – insert new model into the model repository.  (Future) This could validate model format during insertion  **Keep cim/model version in mind
-	modelId – id to store the new model under, or update existing model
-	modelContent – expects either RDF/XML or JSON formatted powergrid model
-	inputFormat – XML/JSON
