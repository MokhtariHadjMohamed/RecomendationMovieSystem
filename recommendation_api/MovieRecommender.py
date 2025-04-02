import joblib
import pandas as pd

knn = joblib.load('knn_model.pkl')
genre_df = joblib.load('genre_matrix.pkl') 
movies_df = joblib.load('movies_metadata.pkl')

class MovieRecommender:
    def __init__(self):
        self.knn = knn
        self.genre_df = genre_df
        self.movies_df = movies_df

    def find_movie_by_name(self, movie_name):
        movie = self.movies_df[self.movies_df['title'].str.contains(movie_name, case=False, na=False)]
        if movie.empty:
            return None
        return movie.iloc[0]

    def recommend_movies_by_name(self, movie_name):
        movie = self.find_movie_by_name(movie_name)
        if movie is None:
            return f"Movie '{movie_name}' not found."
        
        movie_id = movie['movieId']
        movie_index = self.movies_df[self.movies_df['movieId'] == movie_id].index[0]
        movie_vector = self.genre_df.iloc[movie_index].values.reshape(1, -1)
        movie_vector_df = pd.DataFrame(movie_vector, columns=self.genre_df.columns)
        distances, indices = self.knn.kneighbors(movie_vector_df)

        recommendations = []
        for idx in indices[0]:
            if idx != movie_index:
                movie = self.movies_df.iloc[idx]
                recommendations.append({
                    'movieId': int(movie['movieId']),
                    'title': movie['title'],
                    'genres': movie['genres']
                })
        
        return recommendations