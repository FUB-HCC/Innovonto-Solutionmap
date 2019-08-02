"""
The Idea_mapper handles the logic of the application.
The workflow is:
1. create JSON Object from query response
2. generate similarity matrix with specified algorithm (this is passed to Idea_embedder)
3. perform dimensionality reduction on similarity matrix to get coordinates (this is passed to Dimension_reducer)
4. create a JSON-object, where each idea has coordinates


author: Michael Tebbe (michael.tebbe@fu-berlin.de)
"""


import pandas as pd
import json
from random import random
import numpy as np
from dimension_reducer import Dimension_reducer
from idea_embedder import Idea_embedder
class Idea_mapper():
    dimension_reducer = Dimension_reducer()
    idea_embedder = Idea_embedder()
    def map_ideas(self,query_response, similarity_algorithm='USE',dim_reduction_algorithm='PCA'):
        #create JSON Object from query response
        ideas = json.loads(query_response) #pd.read_json(query_response)
        #print(ideas['results']['bindings'][0])
        
        #generate similarity matrix with specified algorithm
        similarity_matrix, labels = self._create_similarity_matrix(ideas,similarity_algorithm)
        
        #perform dimensionality reduction on similarity matrix to get coordinates
        coordinates = self._reduce_dimensions(similarity_matrix, dim_reduction_algorithm)
        
        #create a JSON-object, where each idea has coordinates
        ideas_with_coordinates = self._attach_coordinates_to_ideas(ideas, coordinates, labels)
                
        return ideas_with_coordinates
        



    def _create_similarity_matrix(self,ideas,similarity_algorithm):
        #matrix dimensions are alway equal to the number of ideas
        matrix_dimension = len(ideas['results']['bindings'])

        idea_list = []
        for idea in ideas['results']['bindings']:
            idea_list.append(idea['content']['value'])
        

        matrix_dimension = len(ideas['results']['bindings'])

        similarity_matrix_np = np.random.rand(matrix_dimension,matrix_dimension)
        

        similarity_matrix = pd.DataFrame()
        if similarity_algorithm is 'random':
            #pass randomly initialized similarity matrix
            pass
            

            
        elif similarity_algorithm is 'USE':
            #create similarity matrix from USE embeddings
            similarity_matrix_np = self.idea_embedder.USE(ideas)
        
        
        columns_names = ['dim_'+str(i) for i in range(similarity_matrix_np.shape[1])]
        similarity_matrix = pd.DataFrame(similarity_matrix_np, columns = columns_names)
        labels = self.idea_embedder.cluster_kmeans(similarity_matrix_np)
        

        return similarity_matrix, labels
    


    def _reduce_dimensions(self, similarity_matrix, dim_reduction_algorithm):
        coordinates = similarity_matrix
        
        if dim_reduction_algorithm is 'PCA':
            self.dimension_reducer.tsne(similarity_matrix)

        elif dim_reduction_algorithm is 'TSNE':
            self.dimension_reducer.tsne(similarity_matrix)

        elif dim_reduction_algorithm is 'cut':
            self.dimension_reducer.cut(similarity_matrix)
            #coordinates = similarity_matrix['id']
        
         
        return coordinates[['x','y']]
    


    def _attach_coordinates_to_ideas(self, ideas, coordinates, labels):
        
        for i, idea in enumerate(ideas['results']['bindings']):
            idea['coordinates'] ={}
            idea['coordinates']['x'] = str(coordinates.at[i,'x'])
            idea['coordinates']['y'] = str(coordinates.at[i,'y'])
            idea['cluster_label'] = str(labels[i])
        return ideas