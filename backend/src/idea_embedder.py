import tensorflow as tf
import tensorflow_hub as hub
import numpy as np
import os
import pandas as pd
import re


class Idea_embedder():
    def USE(self, ideas):
        
        idea_list=[]
        #update code to do this only once at the beginning and once at the end!
        for i, idea in enumerate(ideas['results']['bindings']):
            idea_list.append(idea['content']['value'])
            
        #currently uses DAN, switch @param to use transformer 
        module_url = "https://tfhub.dev/google/universal-sentence-encoder/2" #@param ["https://tfhub.dev/google/universal-sentence-encoder/2", "https://tfhub.dev/google/universal-sentence-encoder-large/3"]
        
        # Import the Universal Sentence Encoder's TF Hub module
        embed = hub.Module(module_url)

        
        # Reduce logging output.
        tf.logging.set_verbosity(tf.logging.ERROR)

        with tf.Session() as session:
            session.run([tf.global_variables_initializer(), tf.tables_initializer()])
            message_embeddings = session.run(embed(idea_list))



        #return for now:
        matrix_dimension= len(ideas['results']['bindings'])
        return np.inner(message_embeddings,message_embeddings)