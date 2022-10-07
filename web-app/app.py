import traceback

from flask import Flask
from flask import request
import base64
import time
import os

TIMEOUT = 400
PORT = 8080

app = Flask(__name__)

@app.route('/', methods=['POST', 'GET'])
#@app.route('/')
def process():
    if 'image' not in request.form:
        return 'Missing required image param' , 400
    elif 'category' not in request.form:
        return 'Missing required category param' , 400
    elif 'filename' not in request.form:
        return 'Missing required filename param' , 400
    else:
        category = request.form['category']
        filename = request.form['filename']
        #decode base64 string data
        decoded_data=base64.b64decode((request.form['image']))
        # Check if directory exists        
        if not os.path.isdir(category):
            os.makedirs(category)
        #write the decoded data back to original format in  file
        img_file = open(os.path.join(os.getcwd(), category, filename) , 'wb')
        img_file.write(decoded_data)
        img_file.close()
        print('Success', category, filename)
        return 'Success', 200


# Start the web server
if __name__ == "__main__":
    app.secret_key = ".."
#    app.run(host = 'localhost',port = PORT, debug=False)
    app.run(host = '0.0.0.0',port = PORT, debug=False)
    