# droidex

The application uses a library named Volley to perform HTTP requests, in this case, GET requests to the API and a POST request which can be seen at https://webhook.site/#!/1e63b79c-f943-4cd5-8472-df0e14795ded. 
After receiving data from the API it transforms the JSON objects into POJOs using GSON. Picasso is meant to assist with image loading.

As for the design, the layouts were designed with the Android Material guidelines in mind, this means RecyclerViews, CardViews, BottomSheetDialogs and so on. The application also has different layouts for landscape mode (DetailsActivity in particular). Try pressing a Pokémon's various moves and their abilities.
