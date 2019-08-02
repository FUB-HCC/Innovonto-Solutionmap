"""
The Dimension_reducer takes nD-Vectors and reduces their dimensions to 2 with the specified algorithm.

author: Michael Tebbe (michael.tebbe@fu-berlin.de)
"""

from sklearn.decomposition import PCA
from sklearn.manifold import TSNE
from sklearn.preprocessing import normalize

import numpy as np
import pandas as pd

class Dimension_reducer():
    def pca(self, similarity_matrix):
        """
        Reduce dimensions with PCA
        """
        pca = PCA(n_components=3)
        pca_result = pca.fit_transform(similarity_matrix)
        similarity_matrix['x'] = pca_result[:,0]
        similarity_matrix['y'] = pca_result[:,1]
        
        return similarity_matrix[['x','y']]
    
    def cut(self, similarity_matrix):
        """
        Take the first two dimensions (for testing)
        """
        similarity_matrix['x']= similarity_matrix['dim_0']
        similarity_matrix['y']= similarity_matrix['dim_1']
        
        return similarity_matrix[['x','y']]

    def tsne(self,similarity_matrix):
        """
        Reduce dimensions with t-SNE
        """
        tsne = TSNE(n_components=2, verbose=1, perplexity=30, n_iter=500)
        tsne_result = tsne.fit_transform(similarity_matrix)
        tsne_result =np.divide(tsne_result,4)
        similarity_matrix['x'] = tsne_result[:,0]
        similarity_matrix['y'] = tsne_result[:,1]
        
        return similarity_matrix[['x','y']]


