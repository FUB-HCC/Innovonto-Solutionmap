# Innovonto-Solutionmap
Display Ideas on a HTML SVG element, based on a similariyt pipeline

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

