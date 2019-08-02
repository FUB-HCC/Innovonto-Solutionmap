from sklearn.decomposition import PCA
from sklearn.manifold import TSNE

import numpy as np
import pandas as pd

class Dimension_reducer():
    def pca(self, similarity_matrix):
        pca = PCA(n_components=3)
        pca_result = pca.fit_transform(similarity_matrix)
        similarity_matrix['x'] = pca_result[:,0]
        similarity_matrix['y'] = pca_result[:,1]
        return similarity_matrix[['x','y']]
    
    def cut(self, similarity_matrix):
        similarity_matrix['x']= similarity_matrix['dim_0']
        similarity_matrix['y']= similarity_matrix['dim_1']
        return similarity_matrix[['x','y']]

    def tsne(self,similarity_matrix):
        tsne = TSNE(n_components=2, verbose=1, perplexity=40, n_iter=300)
        tsne_result = tsne.fit_transform(similarity_matrix)
        similarity_matrix['x'] = tsne_result[:,0]
        similarity_matrix['y'] = tsne_result[:,1]
        return similarity_matrix[['x','y']]


