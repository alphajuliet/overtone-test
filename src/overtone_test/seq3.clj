; Probabilistic rhythms
; AJ 2015-10-23

(ns overtone-test.core
  (:use [overtone.live]
        [overtone.inst.drum]))

(def SAMPLES "/Volumes/Galatea 2TB/Music/Samples/")

; Load a TR808 kit
(defsample bd808 (str SAMPLES "drums/tr808/BD/BD0050.wav")) ; kick
(defsample sd808 (str SAMPLES "drums/tr808/SD/SD0050.wav")) ; snare
(defsample ch808 (str SAMPLES "drums/tr808/CH/CH.wav"))     ; closed HH
(defsample oh808 (str SAMPLES "drums/tr808/OH/OH50.wav"))   ; open HH
(defsample rs808 (str SAMPLES "drums/tr808/RS/RS.wav"))     ; rimshot
(defsample cp808 (str SAMPLES "drums/tr808/CP/CP.wav"))     ; clap
(defsample lt808 (str SAMPLES "drums/tr808/LT/LT75.wav"))   ; low tom
(defsample lc808 (str SAMPLES "drums/tr808/LC/LC75.wav"))   ; low conga
(defsample hc808 (str SAMPLES "drums/tr808/HC/HC75.wav"))   ; high conga

;; Probability of playing the note
;; Simple rhythm
(def p-beat1 {bd808 {:vol 1.0 :ptn [1 0 0.1 0]}
              sd808 {:vol 1.0 :ptn [0 0 0 0 1 0 0 0]}
              ch808 {:vol 1.0 :ptn [0 0 1 0]} })

(def live-beats (atom p-beat1))

(def tick 125) ; ms

(defn live-sequencer

  ([curr-t sep-t live-patterns]
   (live-sequencer curr-t sep-t live-patterns 0))

  ([curr-t sep-t live-patterns beat]
   (doseq [[snd {:keys [vol ptn]}] @live-patterns
           :when (< (rand) (nth ptn (mod beat (count ptn))))]
     (at curr-t (snd :amp vol)))

   (let [new-t (+ curr-t sep-t)]
     (apply-by new-t #'live-sequencer [new-t sep-t live-patterns (inc beat)]))))

(live-sequencer (now) tick live-beats)

;; Update rhythm
(swap! live-beats assoc bd808 {:vol 1.0 
                               :ptn [1 0.2 0 0 
                                     1 0 0 0 
                                     1 0 0 0.1 
                                     1 0 0.3 0.2]})

(swap! live-beats assoc sd808 {:vol 1.0
                               :ptn [0 0.1 0 0.2 
                                     1 0 0 0.3]})
(swap! live-beats assoc ch808 {:vol 1.0
                               :ptn [0 0.1 1 0 
                                     0 0.4 1 0]})

(swap! live-beats assoc oh808 {:vol 0.5
                               :ptn [0 0 0 0 
                                     0 0 0 0.5]})
(swap! live-beats assoc rs808 {:vol 0.6 
                               :ptn [0 0 0.8 0 0 0.5 0]})
(swap! live-beats assoc lc808 {:vol 0.8
                               :ptn [0 0 0 0 
                                     0 0 0 0]})
(swap! live-beats assoc hc808 {:vol 0.4
                               :ptn [0 0.2 0 0
                                     0 0 0 0]})

(stop)

(print @live-beats)
