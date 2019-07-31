from sklearn.decomposition import PCA
from sklearn.manifold import TSNE

import numpy as np
import pandas as pd

class Dimension_reducer():
    def pca(self, similarity_matrix):
        pca = PCA(n_components=3)
        pca_matrix = similarity_matrix.drop(['id'], axis = 1)
        pca_result = pca.fit_transform(pca_matrix)
        similarity_matrix['x']=pca_result[:,0]
        similarity_matrix['y']=pca_result[:,1]
        return similarity_matrix[['id','x','y']]


