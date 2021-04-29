CREATE OR REPLACE FUNCTION F_RANDOM_STRING(LENGTH INTEGER)
  RETURNS TEXT AS
$$
DECLARE
  CHARS  TEXT [] := '{0,1,2,3,4,5,6,7,8,9,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z,a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z}';
  RESULT TEXT := '';
  I      INTEGER := 0;
BEGIN
  IF LENGTH < 0
  THEN
    RAISE EXCEPTION 'Given length cannot be less than 0!';
  END IF;
  FOR I IN 1..LENGTH LOOP
    RESULT := RESULT || CHARS [CEIL(61 * RANDOM())];
  END LOOP;
  RETURN RESULT;
END;
$$
LANGUAGE PLPGSQL;