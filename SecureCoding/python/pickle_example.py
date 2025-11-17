import pickle

data = {'key': 'value'}
# Serialization
with open('data.pickle', 'wb') as f:
    pickle.dump(data, f)

# Deserialization
with open('data.pickle', 'rb') as f:
    deserialized = pickle.load(f)
    print(deserialized)

# You can use pickle tools to analyze the content of the pickle file
# python3 -m pickletools data.pickle