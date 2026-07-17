import urllib.request
import json

API_KEY = "PASTE_YOUR_TMDB_READ_ACCESS_TOKEN_HERE"
BASE_URL = "https://api.themoviedb.org/3"
PAGES = 3

CATEGORIES = {
    "NOW_PLAYING": "/movie/now_playing",
    "POPULAR":     "/movie/popular",
    "TOP_RATED":   "/movie/top_rated",
    "UPCOMING":    "/movie/upcoming"
}

def get_trailer_key(movie_id):
    url = f"{BASE_URL}/movie/{movie_id}/videos"
    req = urllib.request.Request(url, headers={"Authorization": f"Bearer {API_KEY}"})
    try:
        with urllib.request.urlopen(req) as response:
            data = json.loads(response.read())
            for video in data.get("results", []):
                if video.get("type") == "Trailer":
                    return video.get("key")
    except Exception as e:
        print(f"  No trailer for movie {movie_id}: {e}")
    return None

result = {}

for category, endpoint in CATEGORIES.items():
    movies = []
    for page in range(1, PAGES + 1):
        url = f"{BASE_URL}{endpoint}?page={page}"
        req = urllib.request.Request(url, headers={"Authorization": f"Bearer {API_KEY}"})
        with urllib.request.urlopen(req) as response:
            data = json.loads(response.read())
            movies.extend(data["results"])
        print(f"Fetched {category} page {page} ({len(data['results'])} movies)")
    result[category] = movies
    print(f"  -> {category} total: {len(movies)} movies\n")

# Dedupe across categories first — a movie in 2+ categories should cost one
# trailer lookup, not one per category it happens to appear in
unique_ids = {movie["id"] for movies in result.values() for movie in movies}
print(f"Fetching trailer keys for {len(unique_ids)} unique movies...")

trailer_keys = {}
for i, movie_id in enumerate(unique_ids, start=1):
    trailer_keys[movie_id] = get_trailer_key(movie_id)
    if i % 20 == 0:
        print(f"  ...{i}/{len(unique_ids)} done")

for movies in result.values():
    for movie in movies:
        movie["trailer_key"] = trailer_keys.get(movie["id"])

output_path = "src/main/resources/seed-movies.json"
with open(output_path, "w", encoding="utf-8") as f:
    json.dump(result, f, ensure_ascii=False)

total = sum(len(v) for v in result.values())
print(f"Done! seed-movies.json written with {total} total entries")
print(f"Output: {output_path}")
