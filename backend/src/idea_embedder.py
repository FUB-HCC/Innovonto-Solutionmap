"""
The Idea_embedder has different algoritms that can be used for generating a similarity matrix from a list of ideas.

1. Assign a vector in nD-space to each Idea with a specified algorithm.
2. Calculate similarty between each pair of vectors. (Atm we use np.inner(). Should be updated) The result of this is a matrix of similarities between ideas.

author: Michael Tebbe (michael.tebbe@fu-berlin.de)
"""

import tensorflow as tf
import tensorflow_hub as hub
from sklearn.cluster import KMeans, OPTICS, cluster_optics_dbscan
from sklearn.decomposition import PCA
import numpy as np
import os
import pandas as pd
import re


class Idea_embedder():
    def USE(self, ideas, distance_metric = 'none'):
        """
        Source: https://colab.research.google.com/github/tensorflow/hub/blob/master/examples/colab/semantic_similarity_with_tf_hub_universal_encoder.ipynb#scrollTo=h1FFCTKm7ba4
        """
        idea_list=ideas
        print (idea_list)
        #update code to do this only once at the beginning and once at the end!
            
        #currently uses DAN, switch @param to use transformer 
        module_url = "https://tfhub.dev/google/universal-sentence-encoder/2" #@param ["https://tfhub.dev/google/universal-sentence-encoder/2", "https://tfhub.dev/google/universal-sentence-encoder-large/3"]
        
        # Import the Universal Sentence Encoder's TF Hub module
        embed = hub.Module(module_url)


        with tf.Session() as session:
            session.run([tf.global_variables_initializer(), tf.tables_initializer()])
            message_embeddings = session.run(embed(idea_list))

        ##pre-reduce dimensions
        #pca = PCA(n_components=100)
        #message_embeddings = pca.fit_transform(message_embeddings)
        #print('Explained variation per principal component: {}'.format(pca.explained_variance_ratio_))
        similarity_matrix = self._calculate_similarity_matrix(message_embeddings, 'none')
        return similarity_matrix

    def _calculate_similarity_matrix(self, vectors, distance_metric = 'none'):
        similarity_matrix = vectors
        if distance_metric is 'none':
            similarity_matrix = vectors
        elif distance_metric is 'inner':
            similarity_matrix = np.inner(vectors, vectors)
        elif distance_metric is 'cosine':
            inner = np.inner(vectors, vectors)
            inner = inner / np.linalg.norm(inner)
            similarity_matrix = inner
        elif distance_metric is 'multi_inner':
            inner = np.inner(vectors, vectors)
            #inner = inner / np.linalg.norm(inner)
            inner = np.inner(vectors, vectors)
            #inner = inner / np.linalg.norm(inner)
            inner = np.inner(vectors, vectors)
            #inner = inner / np.linalg.norm(inner)
            inner = np.inner(vectors, vectors)
            inner = inner / np.linalg.norm(inner)
            
            similarity_matrix = inner
        
        return similarity_matrix

    


