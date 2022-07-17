import numpy as np
from scipy import stats
import base64
import cv2
import pandas as pd





def main(data):
  d_data=base64.b64decode(data)
  np_data=np.fromstring(d_data,np.uint8)
  img_bgr=cv2.imdecode(np_data,cv2.IMREAD_UNCHANGED)
  img=cv2.cvtColor(img_bgr,cv2.COLOR_BGR2GRAY)

  for i in range(0,img.shape[0]):
      for j in range(0,img.shape[1]):
        if img[i][j]<251:
          img[i][j]=0
        else:
          img[i][j]=255


  x=[]
  y=[]
  for i in range(0,img.shape[0]):
    for j in range(0,img.shape[1]):
      if img[i][j]==0:
        x.append(j)
        y.append(i)



  df = pd.DataFrame()
  df['1'] = np.array(x).tolist()
  df['2'] = np.array(y).tolist()


  z_scores = stats.zscore(df)

  abs_z_scores = np.abs(z_scores)
  filtered_entries = (abs_z_scores < 2).all(axis=1)
  new_df = df[filtered_entries]

  new_x=new_df['1'].values
  new_y=new_df['2'].values

  slope, intercept, r, p, std_err = stats.linregress(new_x, new_y)

  def myfunc(x):
    return slope * x + intercept



  mymodel = list(map(myfunc, new_x))


  ymax = np.amax(mymodel)
  ind= mymodel.index(ymax)
  xmax=new_x[ind]
  ymin = np.amin(mymodel)
  ind_= mymodel.index(ymin)
  xmin=new_x[ind_]

  point1 = np.array((xmax,ymax))
  point2 = np.array((xmin,ymin))

  # calculating Euclidean distance
  # using linalg.norm()
  dist = np.linalg.norm(point1 - point2)

  return dist
