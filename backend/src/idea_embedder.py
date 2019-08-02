"""
The Idea_embedder has different algoritms that can be used for generating a similarity matrix from a list of ideas.

1. Assign a vector in nD-space to each Idea with a specified algorithm.
2. Calculate similarty between each pair of vectors. (Atm we use np.inner(). Should be updated) The result of this is a matrix of similarities between ideas.

author: Michael Tebbe (michael.tebbe@fu-berlin.de)
"""

import tensorflow as tf
import tensorflow_hub as hub
from sklearn.cluster import KMeans
import numpy as np
import os
import pandas as pd
import re


class Idea_embedder():
    def USE(self, ideas):
        """
        Source: https://colab.research.google.com/github/tensorflow/hub/blob/master/examples/colab/semantic_similarity_with_tf_hub_universal_encoder.ipynb#scrollTo=h1FFCTKm7ba4
        """
        idea_list=[]
        #update code to do this only once at the beginning and once at the end!
        for i, idea in enumerate(ideas['results']['bindings']):
            idea_list.append(idea['content']['value'])
            
        #currently uses DAN, switch @param to use transformer 
        module_url = "https://tfhub.dev/google/universal-sentence-encoder/2" #@param ["https://tfhub.dev/google/universal-sentence-encoder/2", "https://tfhub.dev/google/universal-sentence-encoder-large/3"]
        
        # Import the Universal Sentence Encoder's TF Hub module
        embed = hub.Module(module_url)


        with tf.Session() as session:
            session.run([tf.global_variables_initializer(), tf.tables_initializer()])
            message_embeddings = session.run(embed(idea_list))



        #return for now:
        matrix_dimension= len(ideas['results']['bindings'])
        similarity_matrix = self._calculate_similarity_matrix(message_embeddings)
        return similarity_matrix

    def _calculate_similarity_matrix(self, vectors):
        algoritm = 'none'
        similarity_matrix = vectors
        if algoritm is 'none':
            similarity_matrix = vectors
        elif algoritm is 'inner':
            similarity_matrix = np.inner(vectors, vectors)
        elif algoritm is 'multi_inner':
            inner = np.inner(vectors, vectors)
            #inner = np.inner(inner,inner)
            
            similarity_matrix = inner
        
        return similarity_matrix

    def cluster_kmeans(self, similarity_matrix):
        kmeans = KMeans(n_clusters=10, random_state=0).fit(similarity_matrix)
        labels = kmeans.labels_
                
        return labels


