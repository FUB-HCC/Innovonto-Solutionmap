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
        similarity_matrix = self._create_similarity_matrix(ideas,similarity_algorithm)
        
        #perform dimensionality reduction on similarity matrix to get coordinates
        coordinates = self._reduce_dimensions(similarity_matrix, dim_reduction_algorithm)
        
        #create a response, where each idea has coordinates
        ideas_with_coordinates = self._attach_coordinates_to_ideas(ideas, coordinates)
                
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
        
        
        columns_names = ['dim_'+str(i) for i in range(matrix_dimension)]
        similarity_matrix = pd.DataFrame(similarity_matrix_np, columns = columns_names)
        

        return similarity_matrix
    


    def _reduce_dimensions(self, similarity_matrix, dim_reduction_algorithm):
        coordinates = similarity_matrix
        
        if dim_reduction_algorithm is 'PCA':
            self.dimension_reducer.pca(similarity_matrix)

        elif dim_reduction_algorithm is 'TSNE':
            self.dimension_reducer.tsne(similarity_matrix)

        elif dim_reduction_algorithm is 'cut':
            self.dimension_reducer.cut(similarity_matrix)
            #coordinates = similarity_matrix['id']
        
        #TODO change clumn names to x,y 
        return coordinates[['x','y']]
    


    def _attach_coordinates_to_ideas(self, ideas, coordinates):
        for i, idea in enumerate(ideas['results']['bindings']):
            
            #print(idea_coordinates.dim_0)
            idea['coordinates'] ={}
            #TODO change clumn names to x,y 
            idea['coordinates']['x'] = coordinates.at[i,'x']
            idea['coordinates']['y'] = coordinates.at[i,'y']
        return ideas