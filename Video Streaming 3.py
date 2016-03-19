from flask import Flask, render_template, Response
import cv2
capture = cv2.VideoCapture(0)

app = Flask(__name__)

@app.route('/')
def index():
    return render_template('index.html')

def gen(capture):
    while True:
        frame = capture.get_frame()
        yield (b'--frame\r\n'
               b'Content-Type: image/jpeg\r\n\r\n' + frame + b'\r\n')

@app.route('/video_feed')
def video_feed():
    return Response(gen(capture()),
                    mimetype='multipart/x-mixed-replace; boundary=frame')

if __name__ == '__main__':
    app.run(host='192.168.1.5', debug=True)