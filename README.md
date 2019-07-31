# Innovonto-Solutionmap
Display Ideas on a HTML SVG element, based on a similarity pipeline

## Frontend
The frontend is build in Clojurescript, using reagent and figwheel.

### Development

To get an interactive development environment run (in folder /frontend):

    lein fig:build

This will auto compile and send all changes to the browser without the
need to reload. After the compilation process is complete, you will
get a Browser Connected REPL. An easy way to try it is:

    (js/alert "Am I connected?")

and you should see an alert in the browser window.

To clean all compiled files:

	lein clean

To create a production build run:

	lein clean
	lein fig:min



### Frontend-Notes

#### Using SVG in HTML:
HTML provides a <svg> element (with width and height)
To draw (for example circles):
 <circle cx="50" cy="50" r="40" stroke="green" stroke-width="4" fill="yellow" />

Predefined Shapes:
 - Rectangle <rect>
 - Circle <circle>
 - Ellipse <ellipse>
 - Line <line>
 - Polyline <polyline>
 - Polygon <polygon>
 - Path <path>

There are filters available for SVG (blur, drop shadows, gradients)

What's a <g> element?

The SVG <g> element is used to group SVG shapes together. Once grouped you can transform the whole group of shapes as if it was a single shape.


##### Rectangle
Example:
        <svg width="400" height="180">
        <rect x="50" y="20" width="150" height="150"
        style="fill:blue;stroke:pink;stroke-width:5;fill-opacity:0.1;stroke-opacity:0.9" />
        </svg>

The x attribute defines the left position of the rectangle (e.g. x="50" places the rectangle 50 px from the left margin)
The y attribute defines the top position of the rectangle (e.g. y="20" places the rectangle 20 px from the top margin)

If you set width/height to a value higher than the svg-size, the element leaves the image (is cut off)

##### Text
<text x="0" y="15" fill="red">I love SVG!</text>

Für mehrere Zeilen:
 <svg height="90" width="200">
  <text x="10" y="20" style="fill:red;">Several lines:
    <tspan x="10" y="45">First line.</tspan>
    <tspan x="10" y="70">Second line.</tspan>
  </text>
</svg>


#### Tooltip
a) Current Coordinates (cx/cy)
b) get transformation matrix from svg object (svg.getScreenCTM)
c) Koordinaten des Tooltips are calculated with:
  - window.getXOffset/window.getYOffset
  - matrix.e/matrix.f (transforemd cx/cy coordinates)
  - + offset of the tooltip itself

see also:
https://msdn.microsoft.com/de-de/library/hh535760(v=vs.85).aspx
https://codepen.io/billdwhite/pen/rgEbc


### Other resources
https://www.reddit.com/r/Clojure/comments/4mf7zs/are_you_drawing_charts_with_clojurescript_if_so/


## Backend 
The backend is built in Python3, using flask as a server.
It provides a REST-API for the frontend.
For machine learning scikit-learn, tensor-flow are used.


###Starting the Server
To start the server, follow these steps:
	(mac/linux)
	
	1. start a Terminal in Folder /backend

	2. Start a virtual environment: 

		 . flask/bin/activate

	3. start flask within the environment (leave out 'FLASK_ENV=development' to start in production mode):

		env FLASK_APP=src/app.py FLASK_ENV=development flask run

	4. Server starts at http://localhost:5000/

	5. Test it with an example GET-request (paste it in your browser URL input field): 

		http://localhost:5000/solutionmap/api/v0.1/get_query_response?query=PREFIX%20gi2mo%3A%3Chttp%3A%2F%2Fpurl.org%2Fgi2mo%2Fns%23%3E%0A%0ASELECT%20%3Fidea%20%3Fcontent%0AWHERE%20%7B%0A%20%20%3Fidea%20a%20gi2mo%3AIdea.%0A%20%20%3Fidea%20gi2mo%3Acontent%20%3Fcontent.%0A%7D%0AORDER%20BY%20%3Fidea

	6.  It should show a JSON object containing a list of ideas and metadata


###REST-API
The REST-API supports the following requests:


####get_query_response:
http://localhost:5000/solutionmap/api/v0.1/get_query_response?query=

Parameters:
query: SparQL Query

Returns the result of a SparQL-query to innovonto-core (https://innovonto-core.imp.fu-berlin.de). The result is a list of ideas with metadata in JSON format. This is to test the API. 


####get_map:

http://localhost:5000/solutionmap/api/v0.1/get_map?query=<YOUR QUERY>&similarity_algorithm=<SIMILARITY ALGORITHM>&dim_reduction_algorithm=<DIMENSIONALITY REDUCTION ALGORITHM>

Parameters:

query: SparQL Query

(optional)
similarity_algorithm : This parameter specifies the algoritm that is used for determining the similarity between ideas. A matrix is produced, that contains the similarity value between each pair of ideas.

possible values:
	'USE' (default): use Universal Sentence Encoder (USE) to produce sentence embeddings and cosine distance to determine similarities
	'random': use random similarity 
	(work in progress)

(optional)
dim_reduction_algorithm : For mapping the ideas on a 2D surface, the dimensions of the similarity matrix need to be reduced to 2. This parameter specifies the algoritm to use for performing dimensionality reduction

	possible values:
		'PCA' (default): uses PCA to perform dimensionality reduction
		'cut': use the first 2 dimensions of the similarity matrix
