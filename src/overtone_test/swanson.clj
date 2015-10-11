;; Code from Erik Swanson's demo on Youtube
;; https://www.youtube.com/watch?v=w7ARayiKBrE

(ns overtone-test.core
  (:use [overtone.live]))

;;(demo 5 (pan2 (sin-osc 440)))

(definst chime [freq 440 dur 1]
  (let [fund freq
        over1 (* 2 fund)
        over2 (* 3.1 fund)
        over3 (* 6.2 fund)]
    (pan2 (* (+ (* (sin-osc fund) 1)
                (* (sin-osc over1) 0.7)
                (* (sin-osc over2) 0.2)
                (* (sin-osc over3) 0.1))
             (env-gen (lin 0 0.1 dur))
             0.2))))

;;(demo 5 (pan2 (* (env-gen (lin 1 2 1)) (sin-osc 440) 0.2)))
;;(demo 5 (pan2 (lf-tri 440)))
;;(demo 5 (pan2 (white-noise)))

(definst hihat [dly 0.1]
  (pan2 (* (env-gen (perc 0 dly 1 -9)) 
           (hpf (white-noise) 9000))))

;; (hihat 0.5)

(definst bass [freq 110 amp 0.8 dur 5]
  (let [fund freq
        over [1 2 3 4 5 6 7 8]
        wt [1 0.1 0.8 0.7 0.6 0.7 0.4 0.2]
        fqs (map (partial * fund) over)
        total-weight (* 0.1 amp)]
    (pan2 (* (sum (map * (map sin-osc fqs) wt))
             (env-gen 
               (lin (* dur 2/5) (* dur 1/5) (* dur 2/5)) 
               :action FREE)
             total-weight))))

;; (bass 55)

(definst choir [freq 880 dur 7]
  (let [fund freq
        fifth (* fund 3/2)
        vib-freq 5
        vib-amp 10]
    (pan2 (* (+ (lf-tri (+ fund (* (sin-osc vib-freq) vib-amp))) 
                (lf-tri (+ fifth (* (sin-osc vib-freq) vib-amp))))
             (- 1 (* (sin-osc 0.5) 0.25))
             (* 0.25 (sin-osc 1))
             (env-gen (lin 0.1 0.1 dur))))))

(defn play-pattern [cur-t sep-t seq sound]
  (at cur-t (when (first seq) (apply sound (first seq))))
  (let [new-t (+ cur-t sep-t)]
    (apply-by new-t #'play-pattern [new-t sep-t (rest seq) sound])))

;;(play-pattern (now) 200 (cycle [[] nil nil [0.5] nil nil [] nil nil]) hihat)
;;(stop)

(defn play-all [sep-t patterns]
  (let [t (+ (now) 200)]
    (doseq [[sound pattern] patterns]
      (play-pattern t sep-t pattern sound))))

(def bpm 200)
(def beat-dur (/ 60 bpm))

(def hat-seq [[] [0.5] nil nil [] nil [0.5] nil])

(def bass-seq (concat [[(midi->hz 36) (* 6 beat-dur)]]
                      (repeat 6 nil)
                      [[(midi->hz 43) (* 6 beat-dur)]]
                      (repeat 5 nil)
                      [[(midi->hz 40) (* 6 beat-dur)]]
                      (repeat 3 nil)))

(def chime-seq (repeatedly #(if-let [a-note (rand-nth [60 64 67 70 72 76 79 nil])]
                              [(midi->hz a-note) (* 1.5 beat-dur)]
                              nil)))

(def choir-seq (repeatedly #(if (< 0.90 (rand)) 
                              [(midi->hz (rand-nth [72 82 88])) (rand-int 8)]
                              nil)))

(play-all 300 {hihat (cycle hat-seq)
               bass (cycle bass-seq)
               chime chime-seq
               choir choir-seq})
(stop)

;; The End
