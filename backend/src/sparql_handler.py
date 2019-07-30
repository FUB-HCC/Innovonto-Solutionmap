from SPARQLWrapper import SPARQLWrapper, JSON

dummy_query = """
PREFIX gi2mo:<http://purl.org/gi2mo/ns#>

SELECT ?idea ?content
WHERE {
  ?idea a gi2mo:Idea.
  ?idea gi2mo:content ?content.
}
ORDER BY ?idea
"""

sparql = SPARQLWrapper("https://innovonto-core.imp.fu-berlin.de/exemplars/query")
sparql.setReturnFormat(JSON)

class Sparql_handler():
  prefix = 'PREFIX gi2mo:<http://purl.org/gi2mo/ns#> '

  def return_response(self, query):
    sparql.setQuery(self.prefix + query)
    results = sparql.query().convert()
    return results