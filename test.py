import tensorflow as tf
import numpy as np

import cv2 as cv

BS = 8
data = []

new_model = tf.keras.models.load_model('covid19.model')
#new_model.summary()

im= cv.imread('our_DATA/negative/IM-0193-0001.jpeg')



image = cv.cvtColor(im, cv.COLOR_BGR2RGB)
#image = cv.cvtColor(frame, cv.COLOR_BGR2GRAY)
image = cv.resize(image, (224, 224))
data.append(image)
data = np.array(data) / 255.0


#loss, acc = new_model.evaluate(image,  test_labels, verbose=2)

predIdxs = new_model.predict(data)
prob_normal = predIdxs[0][1] * 100;
prob_cob    = predIdxs[0][0] * 100;
#predIdxs = np.argmax(predIdxs, axis=1)
print("Probability Positive: %.2f" %  prob_normal)
print("Probability Negative: %.2f" %  prob_cob)

#print(classification_report(testY.argmax(axis=1), predIdxs,
#	target_names=lb.classes_))
