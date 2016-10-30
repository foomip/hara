(ns hara.protocol.archive)

(defprotocol IArchive
  (-url     [archive])
  (-path    [archive entry])
  (-list    [archive])
  (-has?    [archive entry])
  (-archive [archive root inputs])
  (-extract [archive output entries])
  (-insert  [archive entry input])
  (-remove  [archive entry])
  (-stream  [archive entry]))

(defmulti open (fn [type path] type))
