import cv2 as cv
import sys

filename = 'checkers/l/pattern.png'
im = cv.imread(filename,0)
im3 = cv.imread(filename,0)
chessboard_dim = (9,6)
found_all, corners = cv.findChessboardCorners( im, chessboard_dim )
cv.drawChessboardCorners( im3, chessboard_dim, corners, found_all )

cv.imshow("Chessboard with corners", im3)
cv.waitKey()