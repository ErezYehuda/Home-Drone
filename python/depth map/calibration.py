import numpy as np
import cv2
import glob
from matplotlib import pyplot as plt

criteria = (cv2.TERM_CRITERIA_EPS + cv2.TERM_CRITERIA_MAX_ITER, 8, 0.001)
lImgPt = []
rImgPt = []
objpts = []
size=None
CHECKERS = (9, 6)
lImages = glob.glob('checkers/l/*')
rImages = glob.glob('checkers/r/*')

print 'detecting corners\n'

for i in range(len(lImages)):
    imgL = cv2.imread(lImages[i])
    imgR = cv2.imread(rImages[i])

    grayL = cv2.cvtColor(imgL,cv2.COLOR_BGR2GRAY)
    grayR = cv2.cvtColor(imgR,cv2.COLOR_BGR2GRAY)

    size = grayL.shape[::-1];

    ret, cornersL = cv2.findChessboardCorners(grayL, CHECKERS, None)
    ret, cornersR = cv2.findChessboardCorners(grayR, CHECKERS, None)

    cv2.cornerSubPix(grayL,cornersL,(11,11),(-1,-1),criteria)
    cv2.cornerSubPix(grayR,cornersR,(11,11),(-1,-1),criteria)

    objpt = np.zeros((9*6,3), np.float32)
    objpt[:,:2] = np.mgrid[0:9,0:6].T.reshape(-1,2)
    objpts.append(objpt)

    lImgPt.append(cornersL)
    rImgPt.append(cornersR)

    # cv2.drawChessboardCorners(imgL, CHECKERS, cornersL,ret)
    # cv2.imshow('imgL',imgL)
    #
    # cv2.drawChessboardCorners(imgR, CHECKERS, cornersR,ret)
    # cv2.imshow('imgR',imgR)
    # cv2.waitKey()

cv2.destroyAllWindows()

print 'starting calibration'

# ret, mtx, dist, rvecs, tvecs = cv2.calibrateCamera(objpts, rImgPt,grayR.shape[::-1],None,None)
# ret, mtx, dist, rvecs, tvecs = cv2.calibrateCamera(objpts, lImgPt,grayL.shape[::-1],None,None)

retval, cameraMatrix1, distCoeffs1, cameraMatrix2, distCoeffs2, R, T, E, F = cv2.stereoCalibrate(objpts, lImgPt, rImgPt, None, None, None, None, imageSize=(640,480), flags=cv2.CALIB_USE_INTRINSIC_GUESS)

print 'Done Calibration\nStereo rectify\n'

Rl, Rr, Pl, Pr, Q, validPixROI1, validPixROI2 = cv2.stereoRectify(cameraMatrix1, distCoeffs1, cameraMatrix2, distCoeffs2, (640,480), R, T)

print 'Rect done\nUndistort'

lMapX, lMapY = cv2.initUndistortRectifyMap(cameraMatrix1,  distCoeffs1,Rl,None, (640,480), cv2.CV_32FC1)
rMapX, rMapY = cv2.initUndistortRectifyMap(cameraMatrix2, distCoeffs1,Rr,None, (640,480), cv2.CV_32FC1)

print 'Undistort done'

print 'remap'

imgL = cv2.imread('left.jpg',0);
imgR = cv2.imread('right.jpg',0);
cv2.imshow('imgR before ',imgR)
# cv2.imshow('imgL before ',imgL)
imgL = cv2.remap(imgL, lMapX, lMapY, cv2.INTER_LINEAR, cv2.BORDER_CONSTANT)
imgR = cv2.remap(imgR, lMapX, lMapY, cv2.INTER_LINEAR, cv2.BORDER_CONSTANT )
cv2.imshow('imgR after ',imgR)
# cv2.imshow('imgL after ',imgL)

cv2.waitKey()
print 'remap done'


#show the image


