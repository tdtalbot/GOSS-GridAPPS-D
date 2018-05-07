The Powergrid Model Data Manager API allows you to query the powergrid model data store.  Six actions are available: Query_Model_names, Query, Query_Object, Query_Object_Types, Query_Model, and Put_Model

**Query_Model_names** – Returns list of names for all available models.  Allowed parameter is:
-	Result Format – XML/JSON/CSV, Will return results as a list in the format selected.
Example Request:
::
	{
		"requestType": "QUERY_MODEL_NAMES",
		"resultFormat": "JSON"
	}

Example Response for result format JSON:
::
	{ .......
	}



**Query**   - Returns results from a generic SPARQL query against one or all models.
-	modelId  (optional)  - when specified it searches against that model, if empty it will search against all models
-	queryString  - SPARQL query, for more information see https://www.w3.org/TR/rdf-sparql-query/   See below for example.
-	resultFormat – XML/JSON ,   The format you wish the result to be returned in.  Can be either JSON or XML.  Will return result bindings based on the select part of the query string.  See below for example.
Example Request:
::
	{
		"requestType": "QUERY",
		"resultFormat": "JSON",
		"queryString": "select ?line_name ?subregion_name ?region_name WHERE {?line rdf:type cim:Line.?line 	cim:IdentifiedObject.name ?line_name.?line cim:Line.Region ?subregion.?subregion cim:IdentifiedObject.name ?subregion_name.?subregion cim:SubGeographicalRegion.Region ?region.?region cim:IdentifiedObject.name ?region_name}"
	}


Example Response:
::
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


**Query_Object** – Returns details for a particular object based on the object Id
-	modelId (optional) - when specified it searches against that model, if empty it will search against all models
-	objectID – ID of the object you wish to return details for.
-	resultFormat – XML/JSON ,  Will return result bindings based on the select part of the query string.
Example Request:
::
	{
		"requestType": "QUERY_OBJECT",
		"resultFormat": "JSON"
	}
	
**Query_Object_Types** – Returns the available object types in the model
-	modelId (optional) - when specified it searches against that model, if empty it will search against all models
-	resultFormat – XML/JSON /CSV,  Will return results as a list in the format selected.
Example Request:
::
	{
		"requestType": "QUERY_OBJECT_TYPES",
		"resultFormat": "JSON"
	}
	
**Query_Model** – Returns all or part of the specified model.  Can be filtered by object type
-	modelId - when specified it searches against that model, if empty it will search against all models
-	objectType
-	filter – SPARQL formatted filter string
-	resultFormat – XML/JSON,  Will return result in the format selected.
Example Request:
::
	{
		"requestType": "QUERY_MODEL",
		"resultFormat": "JSON"
	}

**Put_Model** – *Not yet available* Inserts a new model into the model repository.  (Future) This could validate model format during insertion  **Keep cim/model version in mind
-	modelId – id to store the new model under, or update existing model
-	modelContent – expects either RDF/XML or JSON formatted powergrid model
-	inputFormat – XML/JSON
