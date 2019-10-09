import json
import pandas as pd
import numpy as np
from idea_embedder import Idea_embedder
import isodate 
import matplotlib.pyplot as plt
import scipy
import sklearn
class Correlator():
    ie = Idea_embedder()
    def calculate_correlation(self, query_response):
        ideas = json.loads(query_response)
        
        #make a list for each column to add to the dataframe
        hit_id_list = []
        worker_list = []
        number_in_session_list = []
        time_since_last_idea_list = []
        idea_content_list = []
        previous_idea_index_list = []
        similarity_list = []
        
        #parse the json object
        for idea in ideas['results']['bindings']:
            hit_id_list.extend([idea['hit']['value']])
            worker_list.extend([idea['worker']['value']])
            number_in_session_list.extend([idea['numberInSession']['value']])
            time_since_last_idea_list.extend([isodate.parse_duration(idea['timeSinceLastIdea']['value']).total_seconds()])
            idea_content_list.extend([idea['content']['value']])

        #make a dataframe
        idea_table = pd.DataFrame()

        #add the lists to the dataframe
        idea_table['hit_id'] = hit_id_list
        idea_table['worker'] = worker_list
        idea_table['number_in_session'] = number_in_session_list
        idea_table['time_since_last_idea'] = time_since_last_idea_list
        idea_table['content'] = idea_content_list
        
        
        #calculate embeddings
        embeddings= self.ie.USE(idea_content_list,'cosine')    

        #find the index of the previous idea for each idea
        for i in range(0,len(idea_table)):
            hit_id = idea_table.loc[i,'hit_id']
            worker = idea_table.loc[i,'worker']
            number_in_session = idea_table.loc[i,'number_in_session']
            previous_idea_index = -1
            #if it's not the first idea in the session
            #find the previous idea's index
            #by the same worker within the same session
            if int(number_in_session) > 1:
                previous_idea_index = idea_table.loc[(idea_table.hit_id == hit_id)
                & (idea_table.worker == worker)
                & (idea_table.number_in_session == str(int(number_in_session) - 1) )].index.item()
            
            previous_idea_index_list.extend([previous_idea_index])
            
            #if idea has a predecessor, look for the corresponding similarity in the embedding matrix
            similarity = -1
            if(previous_idea_index != -1):
                similarity = embeddings[i, previous_idea_index]
            similarity_list.extend([similarity])
        
        print(embeddings)
            
        
        # attach the similarities to the table
        idea_table['previous_idea_index'] = previous_idea_index_list
        idea_table['similarity'] =similarity_list
        
        export_csv = idea_table.to_csv (r'idea_table.csv', header=True) #Don't forget to add '.csv' at the end of the path
        #filter out first ideas from table
        filtered_idea_table = idea_table[idea_table.previous_idea_index != -1]
        #print(filtered_idea_table['similarity'])

        #finally, calculate the corellation between similarity and time to last idea
        filtered_table_np_0 = sklearn.preprocessing.normalize(filtered_idea_table[['time_since_last_idea', 'similarity']].to_numpy(), axis = 0)
        filtered_table_np_1 = sklearn.preprocessing.normalize(filtered_idea_table[['time_since_last_idea', 'similarity']].to_numpy(), axis = 1)

        np.savetxt('similarities_normalized_0.csv', filtered_table_np_0) 
        np.savetxt('similarities_normalized_1.csv', filtered_table_np_1) 
        np.savetxt('similarities.csv', filtered_idea_table[['time_since_last_idea', 'similarity']].to_numpy()) 
        
        print('Pearson 0 :', scipy.stats.pearsonr(filtered_table_np_0[:,0], filtered_table_np_0[:,1]))
        print('Pearson  0 2: ', scipy.stats.pearsonr(filtered_table_np_0[0,:], filtered_table_np_0[1,:]))

        print('Pearson 1 :', scipy.stats.pearsonr(filtered_table_np_1[:,0], filtered_table_np_1[:,1]))
        print('Pearson: 1 2: ', scipy.stats.pearsonr(filtered_table_np_1[0,:], filtered_table_np_1[1,:]))
        #print('Correlation: ', np.correlate(filtered_idea_table[['time_since_last_idea']].to_numpy(), filtered_idea_table[[ 'similarity']].to_numpy()))
        