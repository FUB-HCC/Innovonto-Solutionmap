# Innovonto-Solutionmap
Display Ideas on a HTML SVG element, based on a similarity pipeline



## Frontend
The frontend is build in Clojurescript, using reagent and figwheel.

### Development

To get an interactive development environment run:

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


## Backend 
The backend is built in Python3, using flask as a server. 
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

