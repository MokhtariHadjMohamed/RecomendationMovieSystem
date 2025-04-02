import pandas as pd
from sklearn.preprocessing import MultiLabelBinarizer
from sklearn.neighbors import NearestNeighbors
import joblib

movies_df = pd.read_csv('movies.dat', sep='::', header=None, engine='python',
                        names=['movieId', 'title', 'genres'], encoding='latin-1')

movies_df['genres'] = movies_df['genres'].apply(lambda x: x.split('|'))

mlb = MultiLabelBinarizer()
genre_matrix = mlb.fit_transform(movies_df['genres'])

genre_df = pd.DataFrame(genre_matrix, columns=mlb.classes_)

knn = NearestNeighbors(n_neighbors=5, metric='cosine')
knn.fit(genre_df)

joblib.dump(knn, 'knn_model.pkl')
joblib.dump(genre_df, 'genre_matrix.pkl')
joblib.dump(movies_df, 'movies_metadata.pkl')