import numpy as np
import cv2
import glob
from matplotlib import pyplot as plt

lObjpoints = [] # 3d point in real world space
rObjpoints = [] # 3d point in real world space

lImgpoints = [] # 2d points in image plane.
rImgpoints = [] # 2d points in image plane.

imgL = cv2.imread('left.jpg',0)
imgR = cv2.imread('right.jpg',0)

frames = [imgL, imgR]

#read images
lImages = glob.glob('checkers/l/*')


for fname in lImages:
    img = cv2.imread(fname)
    gray = cv2.cvtColor(img,cv2.COLOR_BGR2GRAY)
    ret = False
    # Find the chess board corners
    ret, corners = cv2.findChessboardCorners(gray, (9,6))
    # If found, add object points, image points (after refining them)
    if ret == True:
        # cv2.cornerSubPix(gray, corners, (11,11), (-1,-1), criteria)
        # Draw and display the corners
        cv2.drawChessboardCorners(img, (9,6), corners, ret)
        cv2.imshow('img',img)
        cv2.waitKey(0)



cv2.destroyAllWindows()

# stereo = cv2.StereoBM_create(numDisparities=16, blockSize=15)
# disparity = stereo.compute(imgL,imgR)
# plt.imshow(disparity,'gray')
# plt.show()