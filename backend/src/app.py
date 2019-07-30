#!flask/bin/python
from flask import Flask, make_response, request, jsonify
from sparql_handler import Sparql_handler
app = Flask(__name__)
dummy_query = """
SELECT ?idea ?content
WHERE {
  ?idea a gi2mo:Idea.
  ?idea gi2mo:content ?content.
}
ORDER BY ?idea
"""
sh = Sparql_handler()

@app.route('/')
def index():
    return "Hello, World!"


#http://localhost:5000/solutionmap/api/v0.1/get_query_response?query=PREFIX%20gi2mo%3A%3Chttp%3A%2F%2Fpurl.org%2Fgi2mo%2Fns%23%3E%0A%0ASELECT%20%3Fidea%20%3Fcontent%0AWHERE%20%7B%0A%20%20%3Fidea%20a%20gi2mo%3AIdea.%0A%20%20%3Fidea%20gi2mo%3Acontent%20%3Fcontent.%0A%7D%0AORDER%20BY%20%3Fidea
@app.route('/solutionmap/api/v0.1/get_query_response', methods=['Get'])
def get_query_response():
  
  query_response = sh.return_response(request.args['query'])
  print 'Return response of query: {}'.format(request.args['query'])
  return jsonify(query_response)
    


if __name__ == '__main__':
    app.run(debug=True)

@app.errorhandler(404)
def not_found(error):
    return make_response(jsonify({'error': 'Not found'}), 404)

