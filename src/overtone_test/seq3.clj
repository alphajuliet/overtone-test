; Probabilistic rhythms
; AJ 2015-10-23

(ns overtone-test.core
  (:use [overtone.live]
        [overtone.inst.drum]))

(def SAMPLES "/Volumes/Galatea 2TB/Music/Samples/")
(defn my-sample [filename]
  (sample (str SAMPLES filename)))

(def bd808 (my-sample "drums/tr808/BD/BD0050.wav")) ; kick
(def sd808 (my-sample "drums/tr808/SD/SD0050.wav")) ; snare
(def ch808 (my-sample "drums/tr808/CH/CH.wav"))     ; closed HH
(def oh808 (my-sample "drums/tr808/OH/OH50.wav"))   ; open HH
(def rs808 (my-sample "drums/tr808/RS/RS.wav"))     ; rimshot
(def cp808 (my-sample "drums/tr808/CP/CP.wav"))     ; clap
(def lt808 (my-sample "drums/tr808/LT/LT75.wav"))   ; low tom
(def lc808 (my-sample "drums/tr808/LC/LC75.wav"))   ; low conga
(def hc808 (my-sample "drums/tr808/HC/HC75.wav"))   ; high conga

;; Probability of playing the note
(def p-beat1 {bd808 [1 0 0.1 0]
              sd808 [0 0 0 0 1 0 0 0]
              ch808 [0 0 1 0]})

(def live-beats (atom p-beat1))

(def tick 125) ; ms

(defn live-sequencer

  ([curr-t sep-t live-patterns]
   (live-sequencer curr-t sep-t live-patterns 0))

  ([curr-t sep-t live-patterns beat]
   (doseq [[sound pattern] @live-patterns
           :when (< (rand) (nth pattern (mod beat (count pattern))))]
     (at curr-t (sound)))

   (let [new-t (+ curr-t sep-t)]
     (apply-by new-t #'live-sequencer [new-t sep-t live-patterns (inc beat)]))))

(live-sequencer (now) tick live-beats)

;; Update rhythm
(swap! live-beats assoc bd808 [1 0.2 0 0 
                               1 0 0 0 
                               1 0 0 0.1 
                               1 0 0.3 0.2])
(swap! live-beats assoc sd808 [0 0.1 0 0.2 
                               1 0 0 0.3])
(swap! live-beats assoc ch808 [0 0.1 1 0 
                               0 0.4 1 0])
(swap! live-beats assoc oh808 [0 0 0 0 
                               0 0 0 0.5])
(swap! live-beats assoc rs808 [0 0 0.8 0 0 0.5 0])
(swap! live-beats assoc lc808 [0 0 0 0 
                               0 0 0 0])
(swap! live-beats assoc hc808 [0 0.2 0 0
                               0 0 0 0])

(stop)
