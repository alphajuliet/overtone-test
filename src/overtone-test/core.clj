(ns overtone-test.core
  (:use [overtone.live]))

(definst foo [freq 220]
  (saw freq))

(demo 7
      (lpf
			 (mix
        (saw [50 (line 100 1600 5) 101 100.5]))
       (lin-lin
        (lf-tri (line 2 20 5))
        -1 1 400 4000)))

(definst saw-wave [freq 440 attack 0.01 sustain 0.4 release 0.1 vol 0.4] 
  (* (env-gen (env-lin attack sustain release) 1 1 0 1 FREE)
     (saw freq)
     vol))

(definst sin-wave [freq 440
                   attack 0.01 sustain 0.4 release 0.1
                   vol 0.4]
  (* (env-gen (lin attack sustain release) 1 1 0 1 FREE)
     (sin-osc freq)
     vol))

(definst spooky-house [freq 440 width 0.2
                       attack 0.3 sustain 4 release 0.3
                       vol 0.4]
  (* (env-gen (lin attack sustain release) 1 1 0 1 FREE)
     (sin-osc (+ freq (* 20 (lf-pulse:kr 0.5 0 width))))
     vol))

(demo 10 (lpf (saw 100) (mouse-x 40 5000 EXP)))

; Mouse x and y control the pitch and cutoff frequency
(demo 10
      (lpf (saw (mouse-y 5000 100 LIN))
           (mouse-x 40 5000 EXP)))




; Play a chord progression in a loop

(defn saw2 [music-note]
      (saw-wave (midi->hz (note music-note))))

(defn play-chord [a-chord]
    (doseq [note a-chord] (saw2 note)))

(defonce metro (metronome 120))
(defn chord-progression-beat [m beat-num]
  (at (m (+ 0 beat-num)) (play-chord (chord :C4 :major)))
  (at (m (+ 4 beat-num)) (play-chord (chord :G3 :major)))
  (at (m (+ 8 beat-num)) (play-chord (chord :A3 :minor)))
  (at (m (+ 12 beat-num)) (play-chord (chord :F3 :major)))
  (apply-at 
    (m (+ 16 beat-num)) 
    chord-progression-beat m (+ 16 beat-num) []))

(chord-progression-beat metro (metro))

; The End
