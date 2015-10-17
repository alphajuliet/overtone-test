; Try some live sequencing
; See https://github.com/overtone/overtone/blob/master/src/overtone/examples/getting_started/pragpub-article.clj
; AJ 2015-10-14

(ns overtone-test.core
  (:use [overtone.live]
        [overtone.inst.drum]))

(def SAMPLES "/Volumes/Galatea 2TB/Music/Samples/")
(defn my-sample [filename]
  (sample (str SAMPLES filename)))

(def bd808 (my-sample "drums/tr808/BD/BD0050.wav"))
(def sd808 (my-sample "drums/tr808/SD/SD0050.wav"))
(def ch808 (my-sample "drums/tr808/CH/CH.wav"))
(def oh808 (my-sample "drums/tr808/OH/OH50.wav"))

(def beat1 {dub-kick [1 0]
            sd808 [0 0 1 0]
            ch808 [0 1]
            oh808 [0 0 0 0 0 0 0 1]})


;; Make a live, editable pattern by using an atom for mutable state

(def live-beats (atom beat1))

(def tick 250) ; ms

(defn live-sequencer
  ([curr-t sep-t live-patterns]
   (live-sequencer curr-t sep-t live-patterns 0))
  ([curr-t sep-t live-patterns beat]
   (doseq [[sound pattern] @live-patterns
           :when (= 1 (nth pattern (mod beat (count pattern))))]
     (at curr-t (sound)))
   (let [new-t (+ curr-t sep-t)]
     (apply-by new-t #'live-sequencer [new-t sep-t live-patterns (inc beat)]))))

(live-sequencer (now) tick live-beats)

(swap! live-beats assoc dub-kick [1 0 1 0 1 0 1 0])
(swap! live-beats assoc sd808 [0 0 1 0 0 0 1 1])
(swap! live-beats assoc ch808 [0 1 1 1 0 1 1 0])

(stop)

