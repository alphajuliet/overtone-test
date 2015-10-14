; Try some basic drum sequencing in Overtone
; AJ 2015-10-10

(ns overtone-test.core
  (:use [overtone.live]))

(def SAMPLES "/Volumes/Galatea 2TB/Music/Samples/")
(defn my-sample [filename]
  (sample (str SAMPLES filename)))

(def bd808 (my-sample "drums/tr808/BD/BD0050.wav"))
(def sd808 (my-sample "drums/tr808/SD/SD0050.wav"))
(def ch808 (my-sample "drums/tr808/CH/CH.wav"))
(def oh808 (my-sample "drums/tr808/OH/OH50.wav"))

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

;; Now, for something a little more sophisticated

(defn play-pattern [curr-t sep-t sound pattern]
  "Play a sound according to a binary pattern at a regular interval."
  (at curr-t (when (= 1 (first pattern)) (sound)))
  (let [new-t (+ curr-t sep-t)]
    (apply-by new-t #'play-pattern [new-t sep-t sound (rest pattern)])))

(def tick 250) ; ms
(def p1 [1 0 1 0 1 0 1 0])
(play-pattern (now) tick bd808 (cycle p1))
(stop)

;; And several at once, in sync
(def beat1 {bd808 [1 0]
            sd808 [0 0 1 0]
            ch808 [0 1]
            oh808 [0 0 0 0 0 0 0 1]})

(def beat2 {bd808 [1 0 0 0 0 1 0 0]
            sd808 [0 0 1 0 0 0 1 0]
            ch808 [1 1 1 1 1 1 1 0]
            oh808 [0 0 0 0 0 0 0 1]})

(defn play-patterns [sep-t patterns]
  (let [t (+ (now) sep-t)]
    (doseq [[sound pattern] patterns]
      (play-pattern t sep-t sound (cycle pattern)))))

(play-patterns tick beat1)
; or
(play-patterns tick beat2)

(stop)
