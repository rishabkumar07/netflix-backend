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

output_path = "src/main/resources/seed-movies.json"
with open(output_path, "w", encoding="utf-8") as f:
    json.dump(result, f, ensure_ascii=False)

total = sum(len(v) for v in result.values())
print(f"Done! seed-movies.json written with {total} total entries")
print(f"Output: {output_path}")
