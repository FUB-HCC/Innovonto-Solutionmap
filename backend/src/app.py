"""
This is the REST-API.
We set the available methods here, make the other modules handle the requests and return responses.

SPARQL-queries go to the Sparql_handler.

The results of the queries go to the Idea_mapper, where the mapping happens.

Source: https://blog.miguelgrinberg.com/post/designing-a-restful-api-with-python-and-flask

author: Michael Tebbe (michael.tebbe@fu-berlin.de)
"""

#!flask/bin/python3
from flask import Flask, make_response, request, jsonify
from flask_cors import CORS
from sparql_handler import Sparql_handler
from idea_mapper import Idea_mapper
from correlator import Correlator
import json
import urllib
app = Flask(__name__)
CORS(app)

dummy_query = """
SELECT ?idea ?content
WHERE {
  ?idea a gi2mo:Idea.
  ?idea gi2mo:content ?content.
}
ORDER BY ?idea
"""
sh = Sparql_handler()
im = Idea_mapper()
cor = Correlator()
@app.route('/')
def index():
    return "Hello, World!"


#Example: http://localhost:5000/solutionmap/api/v0.1/get_query_response?query=PREFIX%20gi2mo%3A%3Chttp%3A%2F%2Fpurl.org%2Fgi2mo%2Fns%23%3E%0A%0ASELECT%20%3Fidea%20%3Fcontent%0AWHERE%20%7B%0A%20%20%3Fidea%20a%20gi2mo%3AIdea.%0A%20%20%3Fidea%20gi2mo%3Acontent%20%3Fcontent.%0A%7D%0AORDER%20BY%20%3Fidea
@app.route('/solutionmap/api/v0.1/get_query_response', methods=['Get'])
def get_query_response():
  app.logger.info('Return response of query:\n{}'.format(request.args['query']))

  #query_response = jsonify(sh.return_response(request.args['query']))
  query_response = sh.return_response(request.args['query'])
  
  return jsonify(query_response)

#Example: http://localhost:5000/solutionmap/api/v0.1/get_map?query=PREFIX%20gi2mo%3A%3Chttp%3A%2F%2Fpurl.org%2Fgi2mo%2Fns%23%3E%0A%0ASELECT%20%3Fidea%20%3Fcontent%0AWHERE%20%7B%0A%20%20%3Fidea%20a%20gi2mo%3AIdea.%0A%20%20%3Fidea%20gi2mo%3Acontent%20%3Fcontent.%0A%7D%0AORDER%20BY%20%3Fidea&similarity_algorithm=USE&dim_reduction_algorithm=PCA
@app.route('/solutionmap/api/v0.1/get_map', methods=['Get'])
def get_map():
  app.logger.info('Create Map of query:\n{}'.format(request.args['query']))

  query_response = jsonify(sh.return_response(request.args['query'])).get_data('results')
  
  similarity_algorithm='USE'
  if hasattr(request.args, 'similarity_algorithm'):
    similarity_algorithm = request.args['similarity_algorithm']

  dim_reduction_algorithm='PCA'
  if hasattr(request.args, 'dim_reduction_algorithm'):
    similarity_algorithm = request.args['dim_reduction_algorithm']

  cluster_method='kmeans'
  if hasattr(request.args, 'cluster_method'):
    similarity_algorithm = request.args['cluster_method']
   
  #,dim_reduction_algorithm='PCA'request.args['query']
  mapping_result = im.map_ideas(query_response = query_response, similarity_algorithm = similarity_algorithm, dim_reduction_algorithm = dim_reduction_algorithm, cluster_method = cluster_method)
  
  return jsonify(mapping_result)

'''
Example:
SELECT ?idea ?worker ?hit ?numberInSession ?timeSinceLastIdea ?content 
WHERE {
   ?idea a gi2mo:Idea;
             gi2mo:hasIdeaContest 
<http://purl.org/innovonto/ideaContests/bionic-radar>;
             gi2mo:content ?content;
             mturk:workerId ?worker;
             mturk:hitId ?hit;
             inov:numberInSession ?numberInSession;
             inov:timeSinceLastIdea ?timeSinceLastIdea.
}
ORDER BY ?worker ?numberInSession
'''

#Example: http://localhost:5000/solutionmap/api/v0.1/correlation_semantic_distance_time?query=PREFIX+gi2mo%3A+%3Chttp%3A%2F%2Fpurl.org%2Fgi2mo%2Fns%23%3E%0D%0APREFIX+mturk%3A+%3Chttp%3A%2F%2Fpurl.org%2Finnovonto%2Fmturk%2F%3E%0D%0APREFIX+inov%3A++%3Chttp%3A%2F%2Fpurl.org%2Finnovonto%2Ftypes%2F%3E%0D%0APREFIX+dcterms%3A+%3Chttp%3A%2F%2Fpurl.org%2Fdc%2Fterms%2F%3E%0D%0APREFIX+rdf%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F1999%2F02%2F22-rdf-syntax-ns%23%3E%0D%0A%0D%0ASELECT+%3Fidea+%3Fworker+%3Fhit+%3FnumberInSession+%3FtimeSinceLastIdea+%3Fcontent+%0D%0AWHERE+%7B%0D%0A+++%3Fidea+a+gi2mo%3AIdea%3B%0D%0A+++++++++++++gi2mo%3AhasIdeaContest+%0D%0A%3Chttp%3A%2F%2Fpurl.org%2Finnovonto%2FideaContests%2Fbionic-radar%3E%3B%0D%0A+++++++++++++gi2mo%3Acontent+%3Fcontent%3B%0D%0A+++++++++++++mturk%3AworkerId+%3Fworker%3B%0D%0A+++++++++++++mturk%3AhitId+%3Fhit%3B%0D%0A+++++++++++++inov%3AnumberInSession+%3FnumberInSession%3B%0D%0A+++++++++++++inov%3AtimeSinceLastIdea+%3FtimeSinceLastIdea.%0D%0A%7D%0D%0AORDER+BY+%3Fworker+%3FnumberInSession
@app.route('/solutionmap/api/v0.1/correlation_semantic_distance_time', methods=['Get'])
def correlation_semantic_distance_time():
  app.logger.info('Calculate correlation between semantic distance between ideas and time betweeen ideas of query query:\n{}'.format(request.args['query']))
  
  
  query_response = jsonify(sh.return_response(request.args['query'])).get_data('results')
  cor.calculate_correlation(query_response)
  #  mapping_result = im.map_ideas(query_response = query_response, similarity_algorithm = similarity_algorithm, dim_reduction_algorithm = dim_reduction_algorithm )
  return jsonify(sh.return_response(request.args['query']))



if __name__ == '__main__':
    app.run(debug=True)

@app.errorhandler(404)
def not_found(error):
    return make_response(jsonify({'error': 'Not found'}), 404)

