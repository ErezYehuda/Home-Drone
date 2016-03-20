import numpy as np
import cv2
from matplotlib import pyplot as plt
capL = cv2.imread('L.png')#cv2.VideoCapture(0)	
capR = cv2.imread('R.png')#cv2.VideoCapture(2)


while 1==1:
	#_, imgL = capL.read()
	#_, imgR = capR.read()
	imgL = capL
	imgR = capR
	L = cv2.cvtColor(imgL, cv2.COLOR_BGR2GRAY)
	R = cv2.cvtColor(imgR, cv2.COLOR_BGR2GRAY)
	cv2.imshow('left',imgL)
	cv2.imshow('right',imgR)
	stereo = cv2.StereoBM_create(numDisparities=16, blockSize=15)
	disparity = stereo.compute(R,L)
	plt.imshow(disparity,'gray')
	plt.show()
	cv2.destroyAllWindows()
