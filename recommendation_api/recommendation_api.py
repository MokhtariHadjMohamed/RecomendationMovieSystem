from flask import Flask, request, jsonify
from MovieRecommender import MovieRecommender

app = Flask(__name__)

recommender = MovieRecommender()

@app.route('/recommend', methods=['GET'])
def recommend_movies():
    movie_name = request.args.get('movie_name')
    if not movie_name:
        return jsonify({'error': 'Movie name is required'}), 400

    recommended_movies = recommender.recommend_movies_by_name(movie_name)
    
    if isinstance(recommended_movies, str):
        return jsonify({'error': recommended_movies}), 404

    return jsonify({'recommendations': [{
        'movieId': int(movie['movieId']),
        'title': movie['title'],
        'genres': movie['genres']
    } for movie in recommended_movies]})

if __name__ == '__main__':
    app.run(debug=True)