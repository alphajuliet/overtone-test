; Try some basic sequencing in Overtone
; AJ 2015-10-10

(ns overtone-test.core
  (:use [overtone.live]))

(def SAMPLES "/Volumes/Galatea 2TB/Music/Samples/")
(defn my-sample [filename]
  (sample (str SAMPLES filename)))

(def bd808 (my-sample "drums/tr808/BD/BD0050.wav"))
(def sd808 (my-sample "drums/tr808/SD/SD0050.wav"))
(def ch808 (my-sample "drums/tr808/CH/CH.wav"))
(def oh808 (my-sample "drums/tr808/OH/OH.wav"))

; setup a tempo for our metronome to use
(def bpm (metronome 120))

; this function will play our sound at whatever tempo we've set our metronome to
(defn looper [nome sound]
  (let [beat (nome)]
    (at (nome beat) (sound))
    (apply-by (nome (inc beat)) 
              looper nome sound [])))

; Turn on the metronome
(looper bpm bd808)

; Stop it
(stop)
