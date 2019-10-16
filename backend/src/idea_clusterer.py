"""
The Idea_clusterer has different algoritms that can be used for generating unsupervised clusters from the similarity matrix.


author: Michael Tebbe (michael.tebbe@fu-berlin.de)
"""

import tensorflow as tf
import tensorflow_hub as hub
from sklearn.decomposition import PCA
from sklearn.cluster import KMeans, OPTICS, cluster_optics_dbscan, SpectralClustering, AgglomerativeClustering
import numpy as np
import os
import pandas as pd
import re


class Idea_clusterer():
    def cluster_kmeans(self, similarity_matrix):
        pca = PCA(n_components=50)
        similarity_matrix = pca.fit_transform(similarity_matrix)
        kmeans = KMeans(n_clusters=50, random_state=0).fit(similarity_matrix)
        labels = kmeans.labels_
                
        return labels

    def cluster_optics(self, similarity_matrix):
        #TODO: Fix
        clust = OPTICS(min_samples=2, xi=0.005)#, min_cluster_size=.05)
        clust.fit(similarity_matrix)
        labels_050 = cluster_optics_dbscan(reachability=clust.reachability_,
                                   core_distances=clust.core_distances_,
                                   ordering=clust.ordering_, eps=100)
        
        labels = clust.labels_[clust.ordering_]
        #labels = labels_050
        labels = np.add(labels,1)
        #labels = labels_050
        return labels
    
    def cluster_spectral(self, similarity_matrix):
        clust = SpectralClustering(n_clusters=60,
        assign_labels="discretize",
        random_state=0)
        clust.fit(similarity_matrix)
        labels = clust.labels_
        return labels

    def cluster_list(self, similarity_matrix, label_list):
        #TODO: The idea is to return labels (which result in colors) based on a list, e.g. a list of authors of ideas
        # insert the list to the set 
        list_set = set(label_list) 
        # convert the set to the list 
        unique_list = (list(list_set)) 
        for x in unique_list:
            pass
        
        labels = np.empty(len(label_list))
    
    def cluster_agglomerative(self, similarity_matrix):
        clust = AgglomerativeClustering(n_clusters=60)
        clust.fit(similarity_matrix)
        labels = clust.labels_
        return labels
        